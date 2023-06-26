package github.jcbsm.bridge;


import github.jcbsm.bridge.discord.ApplicationCommandHandler;
import github.jcbsm.bridge.discord.commands.PlayerListCommand;
import github.jcbsm.bridge.discord.commands.WhitelistCommand;
import github.jcbsm.bridge.exceptions.InvalidConfigException;
import github.jcbsm.bridge.listeners.DiscordChatEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class BridgeDiscordClient {

    private JDA jda;
    private Guild guild;
    private TextChannel chat, console;
    private ApplicationCommandHandler applicationCommandHandler;

    public BridgeDiscordClient(String token, String chatChannelID, String consoleChannelID) throws LoginException, InvalidConfigException, InterruptedException {

        System.out.println("Attempting log in...");
        JDABuilder builder = JDABuilder.createDefault(token,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS
                );

        if (Bridge.getPlugin().getConfig().getBoolean("ChatRelay.Enabled")) {
            System.out.println("Enabling Discord Chat Relay listeners...");
            builder.addEventListeners(new DiscordChatEventListener());
        }

        jda = builder.build();

        // Wait for the client to log in properly
        jda.awaitReady();

        chat = jda.getTextChannelById(chatChannelID);
        console = jda.getTextChannelById(consoleChannelID);
        guild = chat.getGuild();

        System.out.println("Registering Application commands");
        applicationCommandHandler = new ApplicationCommandHandler(
                this,
                new PlayerListCommand(),
                new WhitelistCommand()
        );
    }

    public Guild getGuild() {
        return guild;
    }

    public JDA getJDA() {
        return jda;
    }

    /**
     * Sends a message to the 'chat' discord channel
     * @param content The content of the message sent
     */
    public void sendChatMessage(String content) {

        // Send the message & queue
        chat.sendMessage(content).queue();
    }
}