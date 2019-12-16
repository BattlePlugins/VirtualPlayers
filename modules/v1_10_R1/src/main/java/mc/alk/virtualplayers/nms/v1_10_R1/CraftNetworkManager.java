package mc.alk.virtualplayers.nms.v1_10_R1;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.server.v1_10_R1.EnumProtocolDirection;
import net.minecraft.server.v1_10_R1.NetworkManager;
import net.minecraft.server.v1_10_R1.Packet;

import java.lang.reflect.Field;
import java.net.SocketAddress;

public class CraftNetworkManager extends NetworkManager {

    public CraftNetworkManager() {
        super(EnumProtocolDirection.CLIENTBOUND);

        this.channel = new EmptyNettyChannel(null);
        try {
            Field networkAddress = this.getClass().getField("l");
            networkAddress.setAccessible(true);
            networkAddress.set(this, new SocketAddress() {});
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return super.isConnected();
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        /* do nothing */
    }

    @Override
    public void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        /* do nothing */
    }

    public class EmptyNettyChannel extends AbstractChannel {
        private final ChannelConfig config = new DefaultChannelConfig(this);

        public EmptyNettyChannel(Channel parent) {
            super(parent);
        }

        @Override
        public ChannelConfig config() {
            config.setAutoRead(true);
            return config;
        }

        @Override
        protected void doBeginRead() {
        }

        @Override
        protected void doBind(SocketAddress address) {
        }

        @Override
        protected void doClose() {
        }

        @Override
        protected void doDisconnect() {
        }

        @Override
        protected void doWrite(ChannelOutboundBuffer buffer){
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isCompatible(EventLoop loop) {
            return true;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        protected SocketAddress localAddress0() {
            return null;
        }

        @Override
        public ChannelMetadata metadata() {
            return new ChannelMetadata(true);
        }

        @Override
        protected AbstractUnsafe newUnsafe() {
            return null;
        }

        @Override
        protected SocketAddress remoteAddress0() {
            return null;
        }
    }
}
