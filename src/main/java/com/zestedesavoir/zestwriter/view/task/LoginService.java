package com.zestedesavoir.zestwriter.view.task;

import java.io.IOException;
import java.util.Optional;

import com.zestedesavoir.zestwriter.utils.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

public class LoginService extends Service<Void>{
	private Optional<Pair<String, String>> result;
	private ZdsHttp zdsUtils;
    private Configuration config;
	private final Logger logger;

	public LoginService(Optional<Pair<String, String>> result, ZdsHttp zdsUtils, Configuration config) {
		this.result = result;
		this.zdsUtils = zdsUtils;
        this.config = config;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                result.ifPresent(usernamePassword -> {
                    try {
                        updateMessage("Connexion au site en cours ...");
                        if(zdsUtils.login(usernamePassword.getKey(), usernamePassword.getValue())) {
                            updateMessage("Recherche des contenus ...");
                            zdsUtils.initInfoOnlineContent("tutorial");
                            zdsUtils.initInfoOnlineContent("article");
                        }
                        else {
                            config.resetAuthentification();
                            cancel();
                        }
                    } catch (Exception e) {
                        config.resetAuthentification();
                        cancel();
                    }
                });

                if(!result.isPresent()) {
                    if(zdsUtils.isAuthenticated()) {
                        updateMessage("Recherche des contenus ...");
                        try {
                        	zdsUtils.initInfoOnlineContent("tutorial");
                        	zdsUtils.initInfoOnlineContent("article");
                        } catch (IOException e) {
                            logger.error("", e);
                        }
                    } else {
                        config.resetAuthentification();
                        cancel();
                    }
                }
                return null;
            }
        };
    }
}