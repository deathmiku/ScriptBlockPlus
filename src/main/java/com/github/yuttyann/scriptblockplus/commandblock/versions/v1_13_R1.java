package com.github.yuttyann.scriptblockplus.commandblock.versions;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ChatComponentText;
import net.minecraft.server.v1_13_R1.CommandBlockListenerAbstract;
import net.minecraft.server.v1_13_R1.CommandListenerWrapper;
import net.minecraft.server.v1_13_R1.ICommandListener;
import net.minecraft.server.v1_13_R1.MinecraftServer;
import net.minecraft.server.v1_13_R1.TileEntityCommand;

public final class v1_13_R1 extends Vx_x_Rx {

	@Override
	public int executeCommand(Object iSender, CommandSender bSender, String command) {
		ICommandListener sender = (ICommandListener) iSender;
		MinecraftServer server = ((CraftServer) bSender.getServer()).getServer();
		CommandListenerWrapper wrapper = ((CommandBlockListenerAbstract) sender).getWrapper();
		return server.getCommandDispatcher().dispatchServerCommand(wrapper, command);
	}

	@Override
	public ICommandListener getICommandListener(CommandSender sender, Location location) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		TileEntityCommand tileEntityCommand = new TileEntityCommand();
		tileEntityCommand.setWorld(((CraftWorld) location.getWorld()).getHandle());
		tileEntityCommand.setPosition(new BlockPosition(x, y, z));
		CommandBlockListenerAbstract commandListener = tileEntityCommand.getCommandBlock();
		if (sender != null) {
			commandListener.setName(new ChatComponentText(sender.getName()));
		}
		return commandListener;
	}
}