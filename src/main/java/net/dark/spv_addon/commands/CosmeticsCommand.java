package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.dark.spv_addon.cosmetics.registry.RegisteredCosmetic;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class CosmeticsCommand {
    
    private static final SuggestionProvider<ServerCommandSource> COSMETIC_TYPE_SUGGESTIONS = 
        (context, builder) -> CommandSource.suggestMatching(
            java.util.Arrays.stream(CosmeticType.values()).map(CosmeticType::getId), 
            builder
        );
    
    private static final SuggestionProvider<ServerCommandSource> COSMETIC_ID_SUGGESTIONS = 
        (context, builder) -> {
            try {
                String typeStr = StringArgumentType.getString(context, "type");
                CosmeticType type = CosmeticType.valueOf(typeStr.toUpperCase());
                Map<String, RegisteredCosmetic> cosmetics = SpvCosmetics.getCosmeticsOfType(type);
                return CommandSource.suggestMatching(cosmetics.keySet(), builder);
            } catch (Exception e) {
                return CommandSource.suggestMatching(SpvCosmetics.getAllCosmetics().keySet(), builder);
            }
        };
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cosmetics")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("equip")
                .then(CommandManager.argument("type", StringArgumentType.string())
                    .suggests(COSMETIC_TYPE_SUGGESTIONS)
                    .then(CommandManager.argument("cosmetic", StringArgumentType.string())
                        .suggests(COSMETIC_ID_SUGGESTIONS)
                        .executes(CosmeticsCommand::equipCosmetic))))
            .then(CommandManager.literal("clear")
                .then(CommandManager.argument("type", StringArgumentType.string())
                    .suggests(COSMETIC_TYPE_SUGGESTIONS)
                    .executes(CosmeticsCommand::clearCosmetic))
                .executes(CosmeticsCommand::clearAllCosmetics))
            .then(CommandManager.literal("list")
                .executes(CosmeticsCommand::listCosmetics))
        );
    }
    
    private static int equipCosmetic(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String typeStr = StringArgumentType.getString(context, "type");
        String cosmeticId = StringArgumentType.getString(context, "cosmetic");
        
        try {
            CosmeticType type = CosmeticType.valueOf(typeStr.toUpperCase());
            
            if (SpvCosmetics.equipCosmetic(player, cosmeticId)) {
                RegisteredCosmetic cosmetic = SpvCosmetics.getCosmetic(cosmeticId);
                context.getSource().sendFeedback(() -> 
                    Text.literal("Equipped " + cosmetic.getDisplayName() + " on " + type.getDisplayName()), 
                    false);
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Unknown cosmetic: " + cosmeticId));
                return 0;
            }
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Unknown cosmetic type: " + typeStr));
            return 0;
        }
    }
    
    private static int clearCosmetic(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String typeStr = StringArgumentType.getString(context, "type");
        
        try {
            CosmeticType type = CosmeticType.valueOf(typeStr.toUpperCase());
            SpvCosmetics.unequipCosmetic(player, type);
            context.getSource().sendFeedback(() -> 
                Text.literal("Cleared " + type.getDisplayName() + " cosmetic"), 
                false);
            return 1;
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Unknown cosmetic type: " + typeStr));
            return 0;
        }
    }
    
    private static int clearAllCosmetics(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        for (CosmeticType type : CosmeticType.values()) {
            SpvCosmetics.unequipCosmetic(player, type);
        }
        
        context.getSource().sendFeedback(() -> 
            Text.literal("Cleared all cosmetics"), 
            false);
        return 1;
    }
    
    private static int listCosmetics(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> 
            Text.literal("Available cosmetics:"), 
            false);
        
        for (CosmeticType type : CosmeticType.values()) {
            Map<String, RegisteredCosmetic> cosmetics = SpvCosmetics.getCosmeticsOfType(type);
            if (!cosmetics.isEmpty()) {
                context.getSource().sendFeedback(() -> 
                    Text.literal("  " + type.getDisplayName() + ":"), 
                    false);
                
                for (RegisteredCosmetic cosmetic : cosmetics.values()) {
                    if (!cosmetic.isNone()) {
                        context.getSource().sendFeedback(() -> 
                            Text.literal("    - " + cosmetic.getId() + " (" + cosmetic.getDisplayName() + ")"), 
                            false);
                    }
                }
            }
        }
        
        return 1;
    }
}
