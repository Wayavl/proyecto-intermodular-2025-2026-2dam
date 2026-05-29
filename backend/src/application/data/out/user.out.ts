export interface AuthResponse {
    user_id: string;
    username: string;
}

export interface UserProfileResponse {
    user_id: string;
    username: string;
    email: string;
    join_date: Date;
    last_learn: Date | null;
    premium_expiration_date: Date | null;
    streak: number;
}
