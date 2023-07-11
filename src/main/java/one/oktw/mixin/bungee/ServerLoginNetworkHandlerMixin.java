package one.oktw.mixin.bungee;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.interfaces.BungeeClientConnection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow
    @Final
    ClientConnection connection;

    @Shadow
    GameProfile profile;

    /**
     * initUuid
     */
    @Inject(method = "onHello", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;", shift = At.Shift.AFTER))
    private void fabricproxylegacy$onHello_ServerLoginNetworkHandler$profile(CallbackInfo ci) {
        // override game profile with saved information
        this.profile = new GameProfile(((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedUUID(), this.profile.getName());

        if (((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedProfile() != null) {
            for (Property property : ((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedProfile()) {
                this.profile.getProperties().put(property.getName(), property);
            }
        }
    }

    /**
     * skipKeyPacket
     */
    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean fabricproxylegacy$onHello_MinecraftServer$isOnlineMode(MinecraftServer minecraftServer) {
        return false;
    }
}
