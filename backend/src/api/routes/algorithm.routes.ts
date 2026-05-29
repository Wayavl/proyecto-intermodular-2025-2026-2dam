import { Hono } from "@hono/hono";
import AlgorithmController from "../controller/algorithm-controller.ts";
import { adminMiddleware } from "../middlewares/admin-middleware.ts";

export function setupAlgorithmRoutes(algorithmController: AlgorithmController): Hono {
    const router = new Hono();
    
    // POST request logic where algorithm_id is defined and body represents Controls config
    router.post("/execute/:algorithm_id", (c) => algorithmController.executeAlgorithm(c));

    // GET requests for algorithm info
    router.get("/", (c) => algorithmController.getAlgorithms(c));
    router.get("/:algorithm_id", (c) => algorithmController.getAlgorithm(c));

    router.post("/", adminMiddleware, (c) => algorithmController.createAlgorithm(c));
    router.post("/:algorithm_id/translations", adminMiddleware, (c) => algorithmController.createAlgorithmTranslation(c));
    router.put("/:algorithm_id/premium", adminMiddleware, (c) => algorithmController.updateAlgorithmPremium(c));
    router.put("/:algorithm_id/controls", adminMiddleware, (c) => algorithmController.updateAlgorithmControls(c));
    router.put("/:algorithm_id/translations/:language_name", adminMiddleware, (c) => algorithmController.updateAlgorithmTranslation(c));
    router.delete("/:algorithm_id", adminMiddleware, (c) => algorithmController.deleteAlgorithm(c));

    return router;
}
