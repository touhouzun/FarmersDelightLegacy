package com.wdcftgg.farmersdelightlegacy.common.command;

import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class CommandFarmersDelightLegacy extends CommandBase {

    private static final String commandName = "fd";
    private static final String resetBiomesInitializedCommand = "reSetBiomesInitialized";

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/fd reSetBiomesInitialized [wild_crop]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || args.length > 2 || !resetBiomesInitializedCommand.equals(args[0])) {
            throw new WrongUsageException(getUsage(sender));
        }

        if (args.length == 1) {
            int resetCount = Configuration.resetAllWildCropBiomesInitialized();
            sender.sendMessage(new TextComponentString("Reset biomesInitialized for " + resetCount + " wild crop generators."));
            return;
        }

        if (!Configuration.resetWildCropBiomesInitialized(args[1])) {
            throw new WrongUsageException("Unknown wild crop generator: " + args[1]);
        }
        sender.sendMessage(new TextComponentString("Reset biomesInitialized for " + args[1] + "."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, resetBiomesInitializedCommand);
        }
        if (args.length == 2 && resetBiomesInitializedCommand.equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, Configuration.getWildCropGenerationCategoryPaths());
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
