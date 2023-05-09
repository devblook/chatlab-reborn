package me.bryang.chatlab.command;

import me.bryang.chatlab.file.FileWrapper;
import me.bryang.chatlab.file.type.ConfigurationFile;
import me.bryang.chatlab.file.type.MessagesFile;
import me.bryang.chatlab.manager.SenderManager;
import me.bryang.chatlab.user.User;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;
import team.unnamed.inject.InjectAll;
import team.unnamed.inject.InjectIgnore;

import java.util.Map;

@InjectAll
public class MessageCommand implements CommandClass {

    private FileWrapper<ConfigurationFile> configWrapper;
    private FileWrapper<MessagesFile> messagesWrapper;

    private Map<String, User> users;
    private SenderManager senderManager;
    @InjectIgnore
    private final ConfigurationFile configFile = configWrapper.get();
    @InjectIgnore
    private final MessagesFile messagesFile = messagesWrapper.get();

    @Command(names = {"msg", "pm", "m", "message", "tell", "w"},
            desc = "Private message command")
    public void messageCommand(@Sender Player sender, @OptArg() Player target,
                               @Text @OptArg() String senderMessage) {


        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.noArgumentMessage()
                    .replace("%usage%", "/msg <player> <message>"));
            return;
        }

        if (senderMessage.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.noArgumentMessage()
                    .replace("%usage%", "/msg <player> <message>"));
            return;
        }

        senderManager.sendMessage(sender, configFile.fromSenderMessage()
                .replace("%target%", target.getName())
                .replace("%message%", senderMessage));

        senderManager.sendMessage(target, configFile.toReceptorMessage()
                .replace("%sender%", sender.getName())
                .replace("%message%", senderMessage));

        User senderUser = users.get(sender.getUniqueId().toString());
        User senderTarget = users.get(sender.getUniqueId().toString());

        senderUser.recentMessenger(target.getUniqueId());
        senderTarget.recentMessenger(sender.getUniqueId());
    }
}