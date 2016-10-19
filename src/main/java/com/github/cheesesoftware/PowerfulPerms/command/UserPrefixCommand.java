package com.github.cheesesoftware.PowerfulPerms.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.github.cheesesoftware.PowerfulPerms.common.ICommand;
import com.github.cheesesoftware.PowerfulPermsAPI.PermissionManager;
import com.github.cheesesoftware.PowerfulPermsAPI.PowerfulPermsPlugin;
import com.github.cheesesoftware.PowerfulPermsAPI.Response;
import com.google.common.util.concurrent.ListenableFuture;

public class UserPrefixCommand extends SubCommand {

    public UserPrefixCommand(PowerfulPermsPlugin plugin, PermissionManager permissionManager) {
        super(plugin, permissionManager);
        usage.add("/pp user <user> prefix set/remove <prefix>");
    }

    @Override
    public CommandResult execute(final ICommand invoker, final String sender, final String[] args) throws InterruptedException, ExecutionException {
        if (hasBasicPerms(invoker, sender, "powerfulperms.user.prefix")) {
            if (args != null && args.length >= 2 && args[1].equalsIgnoreCase("prefix")) {
                if (args.length == 3 && !args[2].equalsIgnoreCase("remove")) {
                    sendSender(invoker, sender, getUsage());
                    return CommandResult.success;
                }
                final String playerName = args[0];

                ListenableFuture<UUID> first = permissionManager.getConvertUUID(playerName);
                final UUID uuid = first.get();
                if (uuid == null) {
                    sendSender(invoker, sender, "Could not find player UUID.");
                } else {
                    if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                        String prefix = "";
                        if (args[3].length() >= 1 && args[3].toCharArray()[0] == '"') {
                            // Input is between quote marks.
                            String result = "";
                            result += args[3].substring(1) + " ";

                            if (args.length >= 5) {
                                for (int i = 4; i < args.length; i++) {
                                    result += args[i] + " ";
                                }
                            }

                            if (result.toCharArray()[result.length() - 1] == ' ')
                                result = result.substring(0, result.length() - 1);
                            if (result.toCharArray()[result.length() - 1] == '"')
                                result = result.substring(0, result.length() - 1);

                            prefix = result;
                        } else
                            prefix = args[3];

                        ListenableFuture<Response> second = permissionManager.setPlayerPrefix(uuid, prefix);
                        sendSender(invoker, sender, second.get().getResponse());

                    } else if (args.length >= 3 && args[2].equalsIgnoreCase("remove")) {
                        ListenableFuture<Response> second = permissionManager.setPlayerPrefix(uuid, "");
                        sendSender(invoker, sender, second.get().getResponse());
                    } else {
                        ListenableFuture<String> second = permissionManager.getPlayerOwnPrefix(uuid);
                        sendSender(invoker, sender, "Prefix for player(non-inherited) " + playerName + ": \"" + (second.get() != null ? second.get() : "") + "\"");
                    }
                }
                return CommandResult.success;
            } else
                return CommandResult.noMatch;
        } else
            return CommandResult.noPermission;
    }

    @Override
    public List<String> tabComplete(ICommand invoker, String sender, String[] args) {
        if (args.length == 1 && "prefix".startsWith(args[0].toLowerCase())) {
            List<String> output = new ArrayList<String>();
            output.add("prefix");
            return output;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("prefix")) {
            List<String> output = new ArrayList<String>();
            if ("set".startsWith(args[1].toLowerCase()))
                output.add("set");
            if ("remove".startsWith(args[1].toLowerCase()))
                output.add("remove");
            return output;
        }
        return null;
    }
}
