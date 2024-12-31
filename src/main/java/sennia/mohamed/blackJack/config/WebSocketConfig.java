    package sennia.mohamed.blackJack.config;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.converter.DefaultContentTypeResolver;
    import org.springframework.messaging.converter.MappingJackson2MessageConverter;
    import org.springframework.messaging.converter.MessageConverter;
    import org.springframework.messaging.simp.config.ChannelRegistration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.util.MimeTypeUtils;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    import java.util.List;

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
        @Autowired
        private  MessageLoggingInterceptor messageLoggingInterceptor;
        @Value("${front.url}")
        private String frontUrl;
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/start").setAllowedOrigins(frontUrl).withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/topic","/user");
            registry.setApplicationDestinationPrefixes("/app");
            registry.setUserDestinationPrefix("/user");
        }
        @Override
        public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
            DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
            resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(new ObjectMapper());
            converter.setContentTypeResolver(resolver);
            messageConverters.add(converter);
            return false;
        }
        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
            registration.interceptors(messageLoggingInterceptor);
        }

        @Override
        public void configureClientOutboundChannel(ChannelRegistration registration) {
            registration.interceptors(messageLoggingInterceptor);
        }
    }
