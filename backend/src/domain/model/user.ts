export default interface User {
    user_id: string
    username: string
    email: string
    password_hash: string
    join_date: Date
    last_learn: Date | null
    premium_expiration_date: Date | null
    streak: number
}