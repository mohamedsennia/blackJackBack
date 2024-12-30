package sennia.mohamed.blackJack.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChannelManager {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> channels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> userSubscriptions = new ConcurrentHashMap<>();

    public void addUserToChannel(String channel, String username) {
        // Add user to the channel
        channels.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(username);

        // Track the channel in user's subscriptions
        userSubscriptions.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(channel);
    }

    public void removeUserFromChannel(String channel, String username) {
        // Remove user from the channel
        channels.getOrDefault(channel, new CopyOnWriteArrayList<>()).remove(username);
        if (channels.get(channel).isEmpty()) {
            channels.remove(channel);
        }

        // Remove the channel from user's subscriptions
        userSubscriptions.getOrDefault(username, new CopyOnWriteArrayList<>()).remove(channel);
        if (userSubscriptions.get(username).isEmpty()) {
            userSubscriptions.remove(username);
        }
    }

    public CopyOnWriteArrayList<String> getUsersInChannel(String channel) {
        return channels.getOrDefault(channel, new CopyOnWriteArrayList<>());
    }

    public CopyOnWriteArrayList<String> getChannelsForUser(String username) {
        return userSubscriptions.getOrDefault(username, new CopyOnWriteArrayList<>());
    }
}
