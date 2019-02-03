package com.github.lunatrius.schematica.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.RESET;

public class ClientCommandHandler extends CommandHandler {
	public static final ClientCommandHandler instance = new ClientCommandHandler();

	public String[] latestAutoComplete = null;

	/**
	 * @return 1 if successfully executed, -1 if no permission or wrong usage,
	 * 0 if it doesn't exist or it was canceled (it's sent to the server)
	 */
	@Override
	public int executeCommand(ICommandSender sender, String message) {
		message = message.trim();

		boolean usedSlash = message.startsWith("/");
		if (usedSlash) {
			message = message.substring(1);
		}

		String[] temp = message.split(" ");
		String[] args = new String[temp.length - 1];
		String commandName = temp[0];
		System.arraycopy(temp, 1, args, 0, args.length);
		ICommand icommand = getCommands().get(commandName);

		try {
			if (icommand == null) {
				return 0;
			}

			if (icommand.checkPermission(this.getServer(), sender)) {
				this.tryExecute(sender, args, icommand, message);
				return 1;
			} else {
				sender.sendMessage(format(RED, "commands.generic.permission"));
			}
		} catch (Throwable t) {
			sender.sendMessage(format(RED, "commands.generic.exception"));
		}

		return -1;
	}

	//Couple of helpers because the mcp names are stupid and long...
	private TextComponentTranslation format(TextFormatting color, String str, Object... args) {
		TextComponentTranslation ret = new TextComponentTranslation(str, args);
		ret.getStyle().setColor(color);
		return ret;
	}

	public void autoComplete(String leftOfCursor) {
		latestAutoComplete = null;

		if (leftOfCursor.charAt(0) == '/') {
			leftOfCursor = leftOfCursor.substring(1);

			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen instanceof GuiChat) {
				List<String> commands = getTabCompletions(mc.player, leftOfCursor, mc.player.getPosition());
				if (!commands.isEmpty()) {
					if (leftOfCursor.indexOf(' ') == -1) {
						for (int i = 0; i < commands.size(); i++) {
							commands.set(i, GRAY + "/" + commands.get(i) + RESET);
						}
					} else {
						for (int i = 0; i < commands.size(); i++) {
							commands.set(i, GRAY + commands.get(i) + RESET);
						}
					}

					latestAutoComplete = commands.toArray(new String[commands.size()]);
				}
			}
		}
	}

	@Override
	protected MinecraftServer getServer() {
		return Minecraft.getMinecraft().getIntegratedServer();
	}
}