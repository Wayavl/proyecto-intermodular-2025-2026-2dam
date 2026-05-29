import { Hono } from "@hono/hono";
import ConfigurationController from "../controller/configuration-controller.ts";
import { adminMiddleware } from "../middlewares/admin-middleware.ts";

export function setupConfigurationRoutes(configurationController: ConfigurationController): Hono {
    const router = new Hono();
    
    router.get("/:user_id", (c) => configurationController.getUserConfigurations(c));
    router.post("/:user_id", (c) => configurationController.setUserConfiguration(c));
    router.delete("/:user_id/:configuration_id", (c) => configurationController.resetConfiguration(c));

    router.post("/", adminMiddleware, (c) => configurationController.createConfiguration(c));
    router.post("/:configuration_id/translations", adminMiddleware, (c) => configurationController.createConfigurationTranslation(c));
    router.delete("/:configuration_id", adminMiddleware, (c) => configurationController.deleteConfiguration(c));

    return router;
}
