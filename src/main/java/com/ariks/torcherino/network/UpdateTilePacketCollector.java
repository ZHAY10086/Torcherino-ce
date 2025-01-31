package com.ariks.torcherino.network;

import com.ariks.torcherino.Tiles.TileCollector;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateTilePacketCollector implements IMessage {
    private BlockPos pos;
    private int value;
    public UpdateTilePacketCollector() {}
    public UpdateTilePacketCollector(BlockPos pos, int value) {
        this.value = value;
        this.pos = pos;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.value = buf.readInt();

        }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(value);
    }
    public static class Handler implements IMessageHandler<UpdateTilePacketCollector, IMessage> {
        @Override
        public IMessage onMessage(UpdateTilePacketCollector message, MessageContext ctx) {
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            BlockPos pos = message.pos;
            world.addScheduledTask(() -> {
                if (world.isBlockLoaded(pos)) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileCollector) {
                        int receivedValue = message.value;
                        TileCollector tileCollector = (TileCollector) tile;
                        switch (receivedValue) {
                            case 1: tileCollector.toggleWork();break;
                            case 2: tileCollector.OpenGuiCollector = true;break;
                            case 3: tileCollector.OpenGuiCollector = false;break;
                        }
                    }
                }
            });
            return null;
        }
    }
}