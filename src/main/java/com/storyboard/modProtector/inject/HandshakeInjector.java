package com.storyboard.modProtector.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.storyboard.modProtector.ModProtector;
import com.storyboard.modProtector.util.Reflect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.network.handshake.FMLHandshakeMessage;

public class HandshakeInjector {

    private static Reflect.WrappedField<FMLClientHandler, FMLClientHandler> singletonField;
    private static Reflect.WrappedField<IFMLSidedHandler, FMLCommonHandler> sidedDelegateField;

    static {
        singletonField = Reflect.getField(FMLClientHandler.class, "INSTANCE");
        sidedDelegateField = Reflect.getField(FMLCommonHandler.class, "sidedDelegate");
    }

    private ModProtector mod;

    private NetHandlerPlayClient clientHandler;

    private final Map<Byte, Class<? extends FMLHandshakeMessage>> discriminators = new HashMap<>();

    public HandshakeInjector(final ModProtector mod) {
        this.mod = mod;
        this.clientHandler = null;

        discriminators.put((byte) 0, FMLHandshakeMessage.ServerHello.class);
        discriminators.put((byte) 1, FMLHandshakeMessage.ClientHello.class);
        discriminators.put((byte) 2, FMLHandshakeMessage.ModList.class);
        //discriminators.put((byte) 3, FMLHandshakeMessage.RegistryData.class);
        discriminators.put((byte) -1, FMLHandshakeMessage.HandshakeAck.class);
        discriminators.put((byte) -2, FMLHandshakeMessage.HandshakeReset.class);

        new Thread(() -> {
            mod.getClient().func_152344_a(() -> {
                try {
                    FMLClientHandler patched = this.patchFMLClientHandler();

                    singletonField.unlockFinal();
                    singletonField.set(null, patched);

                    sidedDelegateField.set(FMLCommonHandler.instance(), patched);

                    mod.getLogger().info("Patched FMLClientHandler! " + FMLClientHandler.instance().getClass().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start(); // TASK HACK
    }

    public void onUpdate() {
        clientHandler = (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler();

        this.onDispatcherUpdate();
    }

    protected void onDispatcherUpdate() {
        mod.getLogger().info("Handler updated! -> " + clientHandler);

        if (clientHandler.getNetworkManager().isLocalChannel()) {
            mod.getLogger().info("Local channel detected. disabling...");
            return;
        }

        clientHandler.getNetworkManager().channel().pipeline().addAfter("encoder", "custom_fml_handler",
                new PatchedCustomHandler());
    }

    public class PatchedCustomHandler extends SimpleChannelInboundHandler<Packet> implements ChannelOutboundHandler {

        public PatchedCustomHandler() {

        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
            ctx.fireChannelRead(msg);
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
                throws Exception {
            ctx.bind(localAddress, promise);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
                ChannelPromise promise) throws Exception {
            ctx.connect(remoteAddress, promise);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.disconnect(promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.close(promise);
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.deregister(promise);
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            ctx.read();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof C17PacketCustomPayload) {
                C17PacketCustomPayload payload = (C17PacketCustomPayload) msg;
                if ("FML|HS".equals(payload.func_149559_c())) {
                    ByteBuf buf = Unpooled.wrappedBuffer(payload.func_149558_e());

                    byte discriminator = buf.readByte();
                    Class<? extends FMLHandshakeMessage> clazz = discriminators.get(discriminator);
                    if(clazz == null) {
                        throw new NullPointerException("Wrong payload received " + payload.func_149559_c());
                    }

                    FMLHandshakeMessage decodedMessage = clazz.newInstance();
                    decodedMessage.fromBytes(buf);

                    FMLHandshakeMessage messageToEncode = getMessageToEncode(decodedMessage);

                    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                    buffer.writeByte(discriminator);
                    messageToEncode.toBytes(buffer);

                    ctx.write(new C17PacketCustomPayload(payload.func_149559_c(), buffer));
                    return;
                }
            }

            ctx.write(msg, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        protected FMLHandshakeMessage getMessageToEncode(FMLHandshakeMessage handshakeMessage) {
            if (handshakeMessage instanceof FMLHandshakeMessage.ModList) {
                return new FMLHandshakeMessage.ModList(mod.getOrDefaultModListProxy().getModList());
            }

            return handshakeMessage;
        }
        
    }

    public FMLClientHandler patchFMLClientHandler() throws Exception {
        FMLClientHandler original = FMLClientHandler.instance();
        FMLClientHandler newHandler = new PatchedFMLClientHandler();

        Field[] fieldList = original.getClass().getDeclaredFields();

        for (Field f : fieldList) {
            f.setAccessible(true);
            
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            f.set(newHandler, f.get(original));
        }

        return newHandler;
    }

    public class PatchedFMLClientHandler extends FMLClientHandler {

        @Override
        public void setPlayClient(NetHandlerPlayClient netHandlerPlayClient) {
            super.setPlayClient(netHandlerPlayClient);
            
            onUpdate();
        }

    }

}