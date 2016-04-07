package com.zestedesavoir.zestwriter.view.task;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

public class LoginService extends Service<Void>{
	Optional<Pair<String, String>> result;
	ZdsHttp zdsUtils;
	private final Logger logger;

	public LoginService(Optional<Pair<String, String>> result, ZdsHttp zdsUtils) {
		this.result = result;
		this.zdsUtils = zdsUtils;
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
                            cancel();
                        }
                    } catch (Exception e) {
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
                        cancel();
                    }
                }
                return null;
            }
        };
    }
}