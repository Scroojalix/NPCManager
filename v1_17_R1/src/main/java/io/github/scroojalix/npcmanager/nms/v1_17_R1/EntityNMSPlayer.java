package io.github.scroojalix.npcmanager.nms.v1_17_R1;

import com.mojang.authlib.GameProfile;

import org.bukkit.Location;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSPlayer;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class EntityNMSPlayer extends ServerPlayer implements NMSPlayer {

    public EntityNMSPlayer(MinecraftServer server, ServerLevel world, GameProfile profile, NPCData data) {
        super(server, world, profile);
        Location loc = data.getLoc();
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setRot(loc.getYaw(), loc.getPitch());
        //TODO update this to 1.17
        // SynchedEntityData is the new DataWatcher replacement in 1.17
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
