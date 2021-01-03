package io.github.scroojalix.npcmanager.nms.v1_10_R1;

import com.mojang.authlib.GameProfile;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSPlayer;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.minecraft.server.v1_10_R1.DataWatcherRegistry;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.PlayerInteractManager;
import net.minecraft.server.v1_10_R1.WorldServer;

public class EntityNMSPlayer extends EntityPlayer implements NMSPlayer {
    
    public EntityNMSPlayer(MinecraftServer server, WorldServer world, GameProfile profile, PlayerInteractManager interactManager, NPCData data) {
        super(server, world, profile, interactManager);
        this.setLocation(data.getLoc().getX(), data.getLoc().getY(), data.getLoc().getZ(), data.getLoc().getYaw(), data.getLoc().getPitch());
        this.getDataWatcher().set(DataWatcherRegistry.a.a(13), data.getTraits().getSkinLayersByte());
    }

    @Override
    public GameProfile getProfile() {
        return super.getProfile();
    }
}
