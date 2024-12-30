package sennia.mohamed.blackJack.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import sennia.mohamed.blackJack.config.ChannelManager;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketEventListener {

    private final ChannelManager channelManager;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(ChannelManager channelManager, SimpMessagingTemplate messagingTemplate) {
        this.channelManager = channelManager;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String channel = headerAccessor.getDestination();
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (channel != null && username != null) {
            channelManager.addUserToChannel(channel, username);
        }
    }

    @EventListener
    public void handleDisconnection(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            // Get all channels the user was subscribed to
            CopyOnWriteArrayList<String> channels = channelManager.getChannelsForUser(username);

            // Notify other users in each channel and remove the user
            for (String channel : channels) {
                messagingTemplate.convertAndSend(channel, username + " has disconnected.");
                channelManager.removeUserFromChannel(channel, username);
            }
        }
    }

}
