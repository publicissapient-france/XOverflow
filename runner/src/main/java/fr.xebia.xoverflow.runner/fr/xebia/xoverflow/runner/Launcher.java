package fr.xebia.xoverflow.runner;

import fr.xebia.xoverflow.api.MainHttpEndpoint;
import fr.xebia.xoverflow.backend.es.EsRepository;
import fr.xebia.xoverflow.backend.service.Repository;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        String esUrl = args[0];
        LOGGER.info("Starting Xoverflow with esUrl '{}'.", esUrl);
        Repository repository = new EsRepository(new OkHttpClient(), esUrl);
        MainHttpEndpoint endpoint = new MainHttpEndpoint(8080, repository);
        endpoint.start();
        LOGGER.info("Xoverflow stated.");
    }

}
