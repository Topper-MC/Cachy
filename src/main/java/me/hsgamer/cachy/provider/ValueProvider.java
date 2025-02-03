package me.hsgamer.cachy.provider;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ValueProvider {
    ValueProvider EMPTY = player -> CompletableFuture.completedFuture(Optional.empty());

    CompletableFuture<Optional<String>> get(Player player);
}
