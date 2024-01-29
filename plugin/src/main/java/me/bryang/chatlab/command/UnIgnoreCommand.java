package me.bryang.chatlab.command;

import me.bryang.chatlab.configuration.ConfigurationContainer;
import me.bryang.chatlab.configuration.section.MessageSection;
import me.bryang.chatlab.configuration.section.RootSection;
import me.bryang.chatlab.message.MessageManager;
import me.bryang.chatlab.storage.repository.Repository;
import me.bryang.chatlab.storage.user.User;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import team.unnamed.inject.InjectAll;

import javax.inject.Named;


@InjectAll
public class UnIgnoreCommand implements CommandClass {

	@Named("users")
	private Repository<User> userRepository;

	private ConfigurationContainer<RootSection> configurationContainer;
	private ConfigurationContainer<MessageSection> messageContainer;

	private MessageManager messageManager;

	@Command(
		names = "unignore",
		desc = "Command to un-ignore a player.")
	public void execute(@Sender Player sender, Player target) {

		RootSection rootSection = configurationContainer.get();
		MessageSection messageSection = messageContainer.get();

		if (sender.getUniqueId() == target.getUniqueId()) {
			messageManager.sendMessage(sender, messageSection.error.yourselfIgnore);
			return;
		}

		User user = userRepository.findById(sender.getUniqueId().toString());

		if (!user.containsIgnoredPlayers(target.getUniqueId())) {
			messageManager.sendMessage(sender, messageSection.error.playerAlreadyUnIgnored,
				Placeholder.unparsed("player", target.getName()));
			return;
		}

		user.unIgnore(target.getUniqueId());
		messageManager.sendMessage(sender, rootSection.ignore.unignoredPlayer,
			Placeholder.unparsed("player", target.getName()));
	}
}
