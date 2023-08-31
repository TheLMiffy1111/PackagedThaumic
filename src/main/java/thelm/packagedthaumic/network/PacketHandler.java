package thelm.packagedthaumic.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import thelm.packagedauto.network.ISelfHandleMessage;
import thelm.packagedthaumic.PackagedThaumic;
import thelm.packagedthaumic.network.packet.PacketSyncStability;

public class PacketHandler<REQ extends ISelfHandleMessage<? extends IMessage>> implements IMessageHandler<REQ, IMessage> {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(PackagedThaumic.MOD_ID);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(get(), PacketSyncStability.class, id++, Side.CLIENT);
	}

	public static <REQ extends ISelfHandleMessage<? extends IMessage>> PacketHandler<REQ> get() {
		return new PacketHandler<>();
	}

	@Override
	public IMessage onMessage(REQ message, MessageContext ctx) {
		return message.onMessage(ctx);
	}
}
