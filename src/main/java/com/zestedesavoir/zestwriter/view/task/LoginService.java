package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginService extends Service<Void>{
	private final Logger logger;
	private String username;
	private String password;

	public LoginService(String username, String password) {
        logger = LoggerFactory.getLogger(getClass());
		this.username = username;
		this.password = password;
	}

    public LoginService() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                if(getUsername() != null) {
                    try {
                        updateMessage(Configuration.getBundle().getString("ui.task.auth.prepare.label")+" ...");
                        if(MainApp.getZdsutils().login(getUsername(), getPassword())) {
                            updateMessage(Configuration.getBundle().getString("ui.task.auth.init_content")+" ...");
                            MainApp.getZdsutils().getContentListOnline().clear();
                            MainApp.getZdsutils().initInfoOnlineContent("tutorial");
                            MainApp.getZdsutils().initInfoOnlineContent("article");
                            MainApp.getZdsutils().initInfoOnlineContent("opinion");
                            updateMessage(Configuration.getBundle().getString("ui.task.auth.success.text"));
                        } else {
                            MainApp.getConfig().resetAuthentification();
                            cancel();
                        }
                    } catch (Exception e) {
                        MainApp.getConfig().resetAuthentification();
                        logger.debug(e.getMessage(), e);
                        cancel();
                    }
                } else {
                    if(MainApp.getZdsutils().isAuthenticated()) {
                        updateMessage(Configuration.getBundle().getString("ui.task.auth.init_content")+" ...");
                        try {
                            MainApp.getZdsutils().getContentListOnline().clear();
                            MainApp.getZdsutils().initInfoOnlineContent("tutorial");
                            MainApp.getZdsutils().initInfoOnlineContent("article");
                            MainApp.getZdsutils().initInfoOnlineContent("opinion");
                        } catch (IOException e) {
                            logger.error("Echec de téléchargement des metadonnés des contenus en ligne", e);
                        }
                        updateMessage(Configuration.getBundle().getString("ui.task.auth.success.text"));
                    } else {
                        MainApp.getConfig().resetAuthentification();
                        cancel();
                    }
                }
                return null;
            }
        };
    }
}