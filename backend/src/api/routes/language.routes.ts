import { Hono } from "@hono/hono";
import LanguageController from "../controller/language-controller.ts";
import { adminMiddleware } from "../middlewares/admin-middleware.ts";

export function setupLanguageRoutes(languageController: LanguageController): Hono {
    const router = new Hono();
    
    router.get("/", (c) => languageController.getLanguages(c));
    router.post("/", adminMiddleware, (c) => languageController.createLanguage(c));
    router.delete("/:name", adminMiddleware, (c) => languageController.deleteLanguage(c));

    return router;
}
