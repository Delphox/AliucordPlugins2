/*
 * Ven's Aliucord Plugins
 * Copyright (C) 2021 Vendicated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.Http;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.plugins.hastebin.HasteResponse;
import com.aliucord.plugins.hastebin.PluginSettings;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.models.commands.ApplicationCommandOption;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class Hastebin extends Plugin {
    public Hastebin() {
        super();
        settings = new Settings(PluginSettings.class);
    }

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[] { new Manifest.Author("Vendicated", 343383572805058560L) };
        manifest.description = "Create pastes on hastebin";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/Vendicated/AliucordPlugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        var arguments = new ArrayList<ApplicationCommandOption>();
        arguments.add(new ApplicationCommandOption(ApplicationCommandType.STRING, "text", "The text to upload", null, true, true, null, null));
        arguments.add(new ApplicationCommandOption(ApplicationCommandType.BOOLEAN, "send", "Whether the message should be visible for everyone", null, false, true, null, null));

        commands.registerCommand(
                "haste",
                "Create pastes on hastebin",
                arguments,
                args -> {
                    String text = (String) args.get("text");
                    var _send = args.get("send");
                    boolean send = _send != null && (boolean) _send;

                    String result;
                    String mirror = sets.getString("mirror", "https://haste.powercord.dev") + "/";

                    try {
                        HasteResponse res = Http.simpleJsonPost(mirror + "documents", text, HasteResponse.class);
                        result = mirror + res.key;
                    } catch (IOException ex) {
                        send = false;
                        result = String.format("Error while uploading to hastebin:\n```\n%s```Consider changing hastebin mirror if this keeps happening", ex.getMessage());
                    }

                    return new CommandsAPI.CommandResult(result, null, send);
                }
        );
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}