export interface RegisterUserRequest {
    username: string;
    email: string;
    password_plain: string;
}

export interface LoginUserRequest {
    password_plain: string;
    username?: string;
    email?: string;
}

export interface UpdateUserRequest {
    username: string;
    new_username?: string;
    new_email?: string;
    new_password_plain?: string;
}

export interface GetUserRequest {
    username: string;
}
