package me.hsgamer.cachy.manager;

import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.holder.CacheHolder;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.query.core.QueryManager;
import me.hsgamer.topper.query.simple.SimpleQuery;
import me.hsgamer.topper.query.simple.SimpleQueryContext;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public class CacheQueryManager extends QueryManager<OfflinePlayer> {
    private final List<BiFunction<OfflinePlayer, String, String>> parseFunction = new ArrayList<>();

    public CacheQueryManager(Cachy plugin) {
        addQuery(new SimpleQuery<OfflinePlayer, SimpleQueryContext>() {
            {
                BiFunction<OfflinePlayer, String, @Nullable String> queryFunction = (player, holderName) -> {
                    Optional<CacheHolder> optionalHolder = plugin.get(CacheHolderManager.class).getHolder(holderName);
                    if (!optionalHolder.isPresent()) {
                        return null;
                    }
                    CacheHolder holder = optionalHolder.get();
                    return holder.getEntry(player.getUniqueId()).map(DataEntry::getValue).orElse("");
                };
                registerAction("name", (player, context) -> {
                    OfflinePlayer queryPlayer;
                    if (context.args.isEmpty()) {
                        queryPlayer = player;
                    } else {
                        //noinspection deprecation
                        queryPlayer = plugin.getServer().getOfflinePlayer(parse(player, context.args));
                    }
                    if (queryPlayer == null) return null;
                    return queryFunction.apply(queryPlayer, context.name);
                });
                registerAction("uuid", (player, context) -> {
                    OfflinePlayer queryPlayer;
                    if (context.args.isEmpty()) {
                        queryPlayer = player;
                    } else {
                        try {
                            queryPlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(parse(player, context.args)));
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    if (queryPlayer == null) return null;
                    return queryFunction.apply(queryPlayer, context.name);
                });
            }

            @Override
            protected Optional<SimpleQueryContext> getContext(@NotNull String query) {
                return SimpleQueryContext.fromQuery(query, false);
            }
        });
    }

    public Runnable addParseFunction(BiFunction<OfflinePlayer, String, String> function) {
        parseFunction.add(function);
        return () -> parseFunction.remove(function);
    }

    private String parse(OfflinePlayer player, String query) {
        for (BiFunction<OfflinePlayer, String, String> function : parseFunction) {
            String result = function.apply(player, query);
            if (result != null) {
                query = result;
            }
        }
        return query;
    }
}
