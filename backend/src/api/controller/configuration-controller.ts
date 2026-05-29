import { Context } from "@hono/hono";
import ConfigurationService from "../../application/services/configuration-service.ts";
import { GetUserConfigurationsRequest, SetUserConfigurationRequest, ResetUserConfigurationRequest, CreateConfigurationRequest, CreateConfigurationTranslationRequest, DeleteConfigurationRequest } from "../../application/data/in/configuration.in.ts";
import UserRepository from "../../domain/repository/user-repository.ts";

type JwtPayload = {
    username?: string;
};

export default class ConfigurationController {
    private configurationService: ConfigurationService;
    private userRepository: UserRepository;

    constructor(configurationService: ConfigurationService, userRepository: UserRepository) {
        this.configurationService = configurationService;
        this.userRepository = userRepository;
    }

    private async resolveActingUserId(c: Context, paramUserId: string): Promise<string | Response> {
        const payload = c.get("jwtPayload") as JwtPayload | undefined;
        if (payload?.username === "admin") {
            if (!paramUserId) {
                return c.json({ error: "Missing user_id parameter" }, 400);
            }
            return paramUserId;
        }
        if (!payload?.username) {
            return c.json({ error: "Unauthorized access" }, 401);
        }
        const user = await this.userRepository.readUser(payload.username);
        if (!user) {
            return c.json({ error: "User not found" }, 404);
        }
        return String(user.user_id);
    }

    async getUserConfigurations(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;
            const languageId = c.req.query("lang") || "default_lang_uuid";
            const request: GetUserConfigurationsRequest = { user_id: userId, language_id: languageId };
            const configs = await this.configurationService.getUserConfigurations(request);
            return c.json({ configurations: configs }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async setUserConfiguration(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;

            const body = await c.req.json();
            const { configuration_id, value } = body;

            if (!configuration_id || value === undefined) {
                return c.json({ error: "Missing parameters" }, 400);
            }
            const request: SetUserConfigurationRequest = { user_id: userId, configuration_id, value };
            await this.configurationService.setUserConfigurationValue(request);
            return c.json({ message: "Configuration assigned" }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async resetConfiguration(c: Context) {
        try {
            const paramUserId = c.req.param("user_id");
            const userId = await this.resolveActingUserId(c, paramUserId);
            if (userId instanceof Response) return userId;

            const configurationId = c.req.param("configuration_id");

            if (!configurationId) {
                return c.json({ error: "Missing parameters" }, 400);
            }
            const request: ResetUserConfigurationRequest = { user_id: userId, configuration_id: configurationId };
            await this.configurationService.resetUserConfiguration(request);
            return c.json({ message: "Configuration reset to default" }, 200);
        } catch (error: unknown) {
             if (error instanceof Error) {
                return c.json({ error: error.message }, 500);
            }
            return c.json({ error: "Internal error" }, 500);
        }
    }

    async createConfiguration(c: Context) {
        try {
            const body = await c.req.json();
            if (!body.type) return c.json({ error: "Missing parameters" }, 400);

            const request: CreateConfigurationRequest = { type: body.type };
            const response = await this.configurationService.createConfiguration(request);
            return c.json({ message: "Configuration created", configuration_id: response.configuration_id }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async createConfigurationTranslation(c: Context) {
        try {
            const configurationId = c.req.param("configuration_id");
            const body = await c.req.json();
            if (!configurationId || !body.language_name || !body.configuration_name) return c.json({ error: "Missing parameters" }, 400);

            const request: CreateConfigurationTranslationRequest = { configurationId, languageName: body.language_name, configurationName: body.configuration_name };
            await this.configurationService.createConfigurationTranslation(request);
            return c.json({ message: "Configuration translation created" }, 201);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }

    async deleteConfiguration(c: Context) {
        try {
            const configurationId = c.req.param("configuration_id");
            if (!configurationId) return c.json({ error: "Missing parameters" }, 400);

            const request: DeleteConfigurationRequest = { configurationId };
            await this.configurationService.deleteConfiguration(request);
            return c.json({ message: "Configuration deleted" }, 200);
        } catch (error: unknown) {
            return c.json({ error: (error as Error).message || "Internal error" }, 500);
        }
    }
}
