import UserRepository from "../../domain/repository/user-repository.ts";
import UserAlreadyExists from "../../domain/exceptions/user/user-already-exists.ts";
import UserDoesNotExists from "../../domain/exceptions/user/user-does-not-exists.ts";
import { hash, compare } from "bcrypt";
import UserPasswordIsIncorrect from "../../domain/exceptions/user/user-password-is-incorrect.ts";
import { RegisterUserRequest, LoginUserRequest, GetUserRequest, UpdateUserRequest } from "../data/in/user.in.ts";
import { AuthResponse, UserProfileResponse } from "../data/out/user.out.ts";

export default class UserService {
    private userRepository: UserRepository;

    constructor(userRepository: UserRepository) {
        this.userRepository = userRepository;
    }

    async register(request: RegisterUserRequest): Promise<AuthResponse> {
        // Checking if user already exists would be good, but assuming repository will throw unique constraint or we can check manually:
        const existing = await this.userRepository.readUser(request.username);
        if (existing) {
            throw new UserAlreadyExists("Username is already taken");
        }
        
        const password_hash = await hash(request.password_plain);
        await this.userRepository.register(request.username, request.email, password_hash);
        const created = await this.userRepository.readUser(request.username);
        if (!created) {
            throw new Error("User registration succeeded but profile could not be loaded");
        }
        return { user_id: created.user_id, username: created.username };
    }

    async login(request: LoginUserRequest): Promise<AuthResponse> {
        const identifier = request.username || request.email;
        if (!identifier) {
            throw new Error("Must provide username or email for login");
        }

        const user = await this.userRepository.findByUsernameOrEmail(identifier); 
        // Note: the original PostgresUserRepo has a login method but it requires the actual hash or compares directly.
        // Let's modify the login in repo or just read User by username/email here. Let's just use the repo's login, wait, repo compares hash directly. We can't do that with bcrypt.
        // So we need another method in repo or just use readUser.

        if (!user) {
            throw new UserDoesNotExists("User not found");
        }

        const isValid = await compare(request.password_plain, user.password_hash);
        if (!isValid) {
            throw new UserPasswordIsIncorrect("Incorrect password");
        }

        return { user_id: user.user_id, username: user.username };
    }

    async getUser(request: GetUserRequest): Promise<UserProfileResponse> {
        const user = await this.userRepository.readUser(request.username);
        if (!user) {
            throw new UserDoesNotExists("User not found");
        }
        return {
            user_id: user.user_id,
            username: user.username,
            email: user.email,
            join_date: user.join_date,
            last_learn: user.last_learn,
            premium_expiration_date: user.premium_expiration_date,
            streak: user.streak,
        };
    }

    async updateUsername(request: UpdateUserRequest) {
        if (request.new_username) {
            await this.userRepository.updateUsername(request.username, request.new_username);
        }
    }

    async updateEmail(request: UpdateUserRequest) {
        if (request.new_email) {
            await this.userRepository.updateEmail(request.username, request.new_email);
        }
    }

    async updatePassword(request: UpdateUserRequest) {
        if (request.new_password_plain) {
            const password_hash = await hash(request.new_password_plain);
            await this.userRepository.updatePassword(request.username, password_hash);
        }
    }

    async deleteUser(request: GetUserRequest) {
        await this.userRepository.deleteUser(request.username);
    }

    async addStreak(username: string) {
        await this.userRepository.updateStreak(username);
    }

    async addPremium(username: string, months: number) {
        await this.userRepository.updatePremium(username, months);
    }
}