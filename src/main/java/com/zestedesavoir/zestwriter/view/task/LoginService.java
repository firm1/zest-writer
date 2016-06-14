package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginService extends Service<Void>{
	private ZdsHttp zdsUtils;
    private Configuration config;
	private final Logger logger;
	private String username;
	private String password;

	public LoginService(String username, String password, ZdsHttp zdsUtils, Configuration config) {
	    this(zdsUtils, config);
		this.username = username;
		this.password = password;
	}

	public LoginService(ZdsHttp zdsUtils, Configuration config) {
	    this.zdsUtils = zdsUtils;
	    this.config = config;
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
                        updateMessage(Configuration.bundle.getString("ui.task.auth.prepare.label")+" ...");
                        if(zdsUtils.login(getUsername(), getPassword())) {
                            updateMessage(Configuration.bundle.getString("ui.task.auth.init_content")+" ...");
                            zdsUtils.getContentListOnline().clear();
                            zdsUtils.initInfoOnlineContent("tutorial");
                            zdsUtils.initInfoOnlineContent("article");
                            updateMessage(Configuration.bundle.getString("ui.task.auth.success.text"));
                        } else {
                            config.resetAuthentification();
                            cancel();
                        }
                    } catch (Exception e) {
                        config.resetAuthentification();
                        cancel();
                    }
                } else {
                    if(zdsUtils.isAuthenticated()) {
                        updateMessage(Configuration.bundle.getString("ui.task.auth.init_content")+" ...");
                        try {
                            zdsUtils.getContentListOnline().clear();
                            zdsUtils.initInfoOnlineContent("tutorial");
                            zdsUtils.initInfoOnlineContent("article");
                        } catch (IOException e) {
                            logger.error("Echec de téléchargement des metadonnés des contenus en ligne", e);
                        }
                        updateMessage(Configuration.bundle.getString("ui.task.auth.success.text"));
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