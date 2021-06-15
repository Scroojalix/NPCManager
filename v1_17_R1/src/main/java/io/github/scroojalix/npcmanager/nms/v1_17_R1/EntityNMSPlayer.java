package io.github.scroojalix.npcmanager.nms.v1_17_R1;

import com.mojang.authlib.GameProfile;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSPlayer;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityNMSPlayer extends Player implements NMSPlayer {

    public EntityNMSPlayer(Level world, GameProfile profile, BlockPos position, NPCData data) {
        super(world, position, 0f, profile);
        //TODO update this to 1.17
        // this.getDataWatcher().set(DataWatcherRegistry.a.a(16), data.getTraits().getSkinLayersByte());
    }

    @Override
    public GameProfile getProfile() {
        return super.getGameProfile();
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }
}
