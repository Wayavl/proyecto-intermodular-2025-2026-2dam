import { Context } from "@hono/hono";
import UserRepository from "../../domain/repository/user-repository.ts";

type JwtPayload = {
    id?: string;
    user_id?: string;
    sub?: string;
    username?: string;
};

export function normalizeUserId(id: string | undefined): string | undefined {
    if (id == null) return undefined;
    return String(id).trim().toLowerCase();
}

export function getTokenUserId(payload: JwtPayload | undefined): string | undefined {
    const raw = payload?.id ?? payload?.user_id ?? payload?.sub;
    return normalizeUserId(raw == null ? undefined : String(raw));
}

export async function canAccessUserResource(
    payload: JwtPayload | undefined,
    targetUserId: string,
    userRepository: UserRepository,
): Promise<boolean> {
    if (!targetUserId) return false;
    if (payload?.username === "admin") return true;

    const normalizedTarget = normalizeUserId(targetUserId);
    const tokenUserId = getTokenUserId(payload);
    if (tokenUserId && normalizedTarget && tokenUserId === normalizedTarget) {
        return true;
    }

    if (payload?.username) {
        const user = await userRepository.readUser(payload.username);
        if (user && normalizeUserId(user.user_id) === normalizedTarget) {
            return true;
        }
    }

    if (normalizedTarget) {
        const userById = await userRepository.readUserById(normalizedTarget);
        if (userById && payload?.username && userById.username === payload.username) {
            return true;
        }
    }

    return false;
}

export async function denyUnlessUserResourceAccess(
    c: Context,
    targetUserId: string,
    userRepository: UserRepository,
    message: string,
) {
    const payload = c.get("jwtPayload") as JwtPayload | undefined;
    if (await canAccessUserResource(payload, targetUserId, userRepository)) {
        return null;
    }
    return c.json({ error: message }, 403);
}
