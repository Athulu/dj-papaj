package djpapaj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;

import java.util.*;

public class DJpapaj {
    final static List<List<String>> lista = new ArrayList<>();
    private static final Map<String, Command> commands = new HashMap<>();


    static {
        commands.put("Kto idzie na kremówki?", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Nic więcej nie mów...").block());
    }

    static {
        commands.put("to ja zrobiłem", event -> event.getMessage()
                .getChannel().block()
                .createMessage("nom, to zrobił Nikodem").block());
    }

    static {

        commands.put("no jak tam papaj?", event -> event.getMessage()
                .getChannel().block()
                .createMessage("była bycza").block());
    }

    static {
        commands.put("help", event -> event.getMessage()
                .getChannel().block()
                .createMessage("◉ Kto idzie na kremówki? - zbieranie ekipy\n" +
                        "◉ dzieci - :)\n" +
                        "◉ play - + link do piosenki z youtube'a\n" +
                        "◉ help - lista komend").block());
    }




    public static void main(String[] args) {
        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
                .login()
                .block();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    // 3.1 Message.getContent() is a String
                    final String content = event.getMessage().getContent();

                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        // We will be using ! as our "prefix" to any command in the system.
                        if (content.startsWith("" + entry.getKey())) { //'#'
                            entry.getValue().execute(event);
                            break;
                        }
                    }
                });

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        // This is an optimization strategy that Discord4J can utilize.
        // It is not important to understand
        playerManager.getConfiguration()
                .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();

        // We will be creating LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        final TrackScheduler scheduler = new TrackScheduler(player);

        commands.put("play", event -> {

            //JOIN
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();
                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();
                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        channel.join(spec -> spec.setProvider(provider)).block();
                    }
                }
            }

            //PLAY
            String content = event.getMessage().getContent();
            List<String> command = Arrays.asList(content.split(" "));
            lista.add(Arrays.asList(content.split(" ")));
            playerManager.loadItem(command.get(1), scheduler);
            //lista.add(Arrays.asList("!play https://www.youtube.com/watch?v=LgOcy0jr4wQ".split(" ")));
            //System.out.println("Jebać disa: ");
            //playerManager.loadItemOrdered(lista.get(0), lista.get(0).get(1), scheduler);

        });
        client.onDisconnect().block();
    }
}
