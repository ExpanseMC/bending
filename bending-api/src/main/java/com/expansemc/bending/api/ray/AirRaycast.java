package com.expansemc.bending.api.ray;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Predicates to help facilitate advancing rays for Air-based abilities.
 */
public final class AirRaycast {

    /**
     * Removes fire blocks at the provided {@link ServerLocation}.
     *
     * @param test The block to remove fire from.
     * @return True if the fire was extinguished, false otherwise.
     */
    public static boolean extinguishFlames(final ServerLocation test) {
        return test.getBlockType().isAnyOf(BlockTypes.FIRE.get(), BlockTypes.SOUL_FIRE.get())
                && test.removeBlock();
    }

    /**
     * Toggles the open state of the block at the provided
     * {@link ServerLocation}.
     *
     * @param test The block to toggle.
     * @return True if the block's open state was toggled, false otherwise.
     */
    public static boolean toggleOpenable(final ServerLocation test) {
        if (!test.supports(Keys.IS_OPEN)) {
            return false;
        }

        final DataTransactionResult result = test.offer(Keys.IS_OPEN, test.getOrElse(Keys.IS_OPEN, false));
        return result.isSuccessful();
    }

    /**
     * Toggles the powered state of the block at the provided
     * {@link ServerLocation}.
     *
     * @param test The block to toggle.
     * @return True if the block's powered state was toggled, false otherwise.
     */
    public static boolean togglePowerable(final ServerLocation test) {
        if (!test.supports(Keys.IS_POWERED)) {
            return false;
        }

        final DataTransactionResult result = test.offer(Keys.IS_POWERED, test.getOrElse(Keys.IS_POWERED, false));
        return result.isSuccessful();
    }

    private AirRaycast() {
    }
}