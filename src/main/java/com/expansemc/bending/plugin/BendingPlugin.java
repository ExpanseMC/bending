package com.expansemc.bending.plugin;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategory;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;
import com.expansemc.bending.api.bender.Bender;
import com.expansemc.bending.api.data.BendingKeys;
import com.expansemc.bending.api.registry.BendingRegistryTypes;
import com.expansemc.bending.plugin.ability.AbilityCategoryImpl;
import com.expansemc.bending.plugin.ability.AbilityControlImpl;
import com.expansemc.bending.plugin.ability.AbilityImpl;
import com.expansemc.bending.plugin.ability.execution.AbilityCauseFactory;
import com.expansemc.bending.plugin.ability.task.AbilityTaskFactory;
import com.expansemc.bending.plugin.ability.task.AbilityTaskResultFactory;
import com.expansemc.bending.plugin.bender.BenderFactory;
import com.expansemc.bending.plugin.command.CommandBending;
import com.expansemc.bending.plugin.listener.AbilityControlListener;
import com.expansemc.bending.plugin.listener.AbilityHudListener;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataStore;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterBuilderEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.api.event.lifecycle.RegisterFactoryEvent;
import org.spongepowered.api.event.lifecycle.RegisterRegistryEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Plugin("bending")
public final class BendingPlugin implements Bending {

    private final Logger logger;
    private final PluginContainer container;

    @Inject
    public BendingPlugin(final Logger logger, final PluginContainer container) {
        this.logger = logger;
        this.container = container;
    }

    @Listener
    public void onRegisterFactory(final RegisterFactoryEvent event) {
        this.logger.info("Registering factories...");

        event.register(AbilityCause.Factory.class, new AbilityCauseFactory());
        event.register(AbilityTask.Factory.class, new AbilityTaskFactory());
        event.register(AbilityTaskResult.Factory.class, new AbilityTaskResultFactory());
        event.register(Bender.Factory.class, BenderFactory.INSTANCE);
        event.register(Bending.class, this);
    }

    @Listener
    public void onRegisterBuilder(final RegisterBuilderEvent event) {
        this.logger.info("Registering builders...");

        event.register(Ability.Builder.class, AbilityImpl.BuilderImpl::new);
        event.register(AbilityCategory.Builder.class, AbilityCategoryImpl.BuilderImpl::new);
        event.register(AbilityControl.Builder.class, AbilityControlImpl.BuilderImpl::new);
    }

    @Listener
    public void onRegisterCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        this.logger.info("Registering commands...");

        event.register(this.container, CommandBending.COMMAND, "bending", "b");
    }

    @SuppressWarnings("unchecked")
    @Listener
    public void onRegisterData(final RegisterDataEvent event) {
        this.logger.info("Registering data...");

        final DataStore storeAbilityHotbar = DataStore.builder()
                .pluginData(Bending.key("ability"))
                .holder(Player.class, User.class)
                .key(BendingKeys.ABILITY_HOTBAR, this::storeAbilityHotbar, this::loadAbilityHotbar)
                .key(BendingKeys.PASSIVE_ABILITIES, this::storePassiveAbilities, this::loadPassiveAbilities)
                .build();

        event.register(DataRegistration.builder()
                .dataKey(BendingKeys.ABILITY_HOTBAR)
                .store(storeAbilityHotbar)
                .build());
    }

    private void storeAbilityHotbar(final DataView outer, final Map<Integer, Ability> map) {
        final DataView view = outer.createView(DataQuery.of("AbilityHotbar"));
        for (final Map.Entry<Integer, Ability> entry : map.entrySet()) {
            view.set(DataQuery.of(entry.getKey().toString()), entry.getValue().key(BendingRegistryTypes.ABILITY));
        }
    }

    private Optional<Map<Integer, Ability>> loadAbilityHotbar(final DataView outer) {
        final @Nullable DataView view = outer.getView(DataQuery.of("AbilityHotbar")).orElse(null);
        if (view == null) {
            return Optional.of(Map.of());
        }
        final Map<Integer, Ability> result = new HashMap<>();
        for (final DataQuery key : view.keys(false)) {
            view.getRegistryValue(key, BendingRegistryTypes.ABILITY).ifPresent(ability -> {
                try {
                    result.put(Integer.parseInt(key.toString()), ability);
                } catch (final NumberFormatException ignored) {
                }
            });
        }
        return Optional.of(result);
    }

    private void storePassiveAbilities(final DataView view, final Set<Ability> list) {
        final Set<ResourceKey> keys = list.stream()
                .flatMap(ability -> ability.findKey(BendingRegistryTypes.ABILITY).stream())
                .collect(Collectors.toSet());
        view.set(DataQuery.of("PassiveAbilities"), keys);
    }

    private Optional<Set<Ability>> loadPassiveAbilities(final DataView view) {
        return view.getResourceKeyList(DataQuery.of("PassiveAbilities"))
                .map(keys -> keys.stream()
                        .flatMap(key -> BendingRegistryTypes.ABILITY.get().findValue(key).stream())
                        .collect(Collectors.toSet()));
    }

    @Listener
    public void onRegisterRegistry(final RegisterRegistryEvent event) {
        this.logger.info("Registering registries...");

        event.register(BendingRegistryTypes.ABILITY_CATEGORY.location(), true, () -> Map.of(
                Bending.key("air"), AbilityCategory.of(Component.text("Air", NamedTextColor.GRAY)),
                Bending.key("earth"), AbilityCategory.of(Component.text("Earth", NamedTextColor.GREEN)),
                Bending.key("fire"), AbilityCategory.of(Component.text("Fire", NamedTextColor.RED)),
                Bending.key("water"), AbilityCategory.of(Component.text("Water", NamedTextColor.AQUA))
        ));
        event.register(BendingRegistryTypes.ABILITY_CONTROL.location(), true, () -> Map.of(
                Bending.key("fall"), AbilityControl.of(Component.text("Fall")),
                Bending.key("passive"), AbilityControl.of(Component.text("Passive")),
                Bending.key("primary"), AbilityControl.of(Component.keybind("key.attack")),
                Bending.key("secondary"), AbilityControl.of(Component.keybind("key.use")),
                Bending.key("sneak"), AbilityControl.of(Component.keybind("key.sneak"))
        ));
        event.register(BendingRegistryTypes.ABILITY.location(), true);
    }

    @Listener
    public void onStartingServer(final StartingEngineEvent<Server> event) {
        this.logger.info("Registering listeners...");

        Sponge.eventManager().registerListeners(this.container, new AbilityControlListener());
        Keys.IS_SNEAKING.registerEvent(this.container, Player.class, AbilityControlListener.ON_SNEAK);
        Sponge.eventManager().registerListeners(this.container, new AbilityHudListener());
        Sponge.eventManager().registerListeners(this.container, BenderFactory.INSTANCE);
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public PluginContainer implementation() {
        return this.container;
    }
}