package io.github.scroojalix.npcmanager.nms.v1_8_R2;

import com.mojang.authlib.GameProfile;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSPlayer;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.PlayerInteractManager;
import net.minecraft.server.v1_8_R2.WorldServer;

public class EntityNMSPlayer extends EntityPlayer implements NMSPlayer {
    
    public EntityNMSPlayer(MinecraftServer server, WorldServer world, GameProfile profile, PlayerInteractManager interactManager, NPCData data) {
        super(server, world, profile, interactManager);
        this.setLocation(data.getLoc().getX(), data.getLoc().getY(), data.getLoc().getZ(), data.getLoc().getYaw(), data.getLoc().getPitch());
        this.getDataWatcher().watch(10, data.getTraits().getSkinLayersByte());
    }

    @Override
    public GameProfile getProfile() {
        return super.getProfile();
    }
}
