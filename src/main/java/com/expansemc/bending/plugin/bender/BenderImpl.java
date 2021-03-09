package com.expansemc.bending.plugin.bender;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import com.expansemc.bending.api.bender.Bender;
import com.expansemc.bending.api.util.Completion;
import com.expansemc.bending.plugin.ability.execution.AbilityContextImpl;
import com.expansemc.bending.plugin.util.CallbackCompletion;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class BenderImpl implements Bender {

    private final UUID uuid;

    private final Table<Ability, AbilityControl, Completion> completions = HashBasedTable.create();
    private final Set<AbilityContext> contexts = new HashSet<>();

    public BenderImpl(final UUID uuid) {
        this.uuid = uuid;
    }

    public final Completion defer(final Ability ability, final AbilityControl control) {
        final Completion completion = new CallbackCompletion(
                () -> this.completions.remove(ability, control),
                () -> this.completions.remove(ability, control)
        );

        this.completions.put(ability, control, completion);
        return completion;
    }

    public void cancelAll() {
        this.completions.values().forEach(Completion::cancel);
        this.contexts.forEach(AbilityContext::cancel);
    }

    public void removeContext(final AbilityContext context) {
        this.contexts.remove(context);
    }

    @Override
    public Optional<AbilityContext> execute(final AbilityCause cause) {
        if (!cause.ability().controls().contains(cause.control())) {
            // The ability doesn't support the provided control.
            return Optional.empty();
        }

        final @Nullable Completion completion = this.completions.remove(cause.ability(), cause.control());
        if (completion != null) {
            completion.complete();
            return Optional.empty();
        }

        // TODO: check and set cooldown

        final AbilityContext context = new AbilityContextImpl(this, cause);

        try {
            cause.ability().executor().execute(context);
        } catch (final AbilityException e) {
            cause.audience().sendMessage(e.toComponent(() -> Component.text("An error occurred while executing ").append(cause.ability().name())));
            return Optional.empty();
        }

        if (context.isFinished()) {
            return Optional.empty();
        }

        this.contexts.add(context);
        return Optional.of(context);
    }
}