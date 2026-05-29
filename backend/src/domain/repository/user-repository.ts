import User from "../model/user.ts";

export default interface UserRepository {
  /**
   * @param password Users password already hashed
   * @param username Users username, It can be null if email is present
   * @param email Users email, It can be null if email is present
   */
  login(
    password: string,
    username?: string,
    email?: string,
  ): Promise<{ username: string }>;

  /**
   * @param username User's username.
   * @param email User's email.
   * @param password User's password.
   */
  register(
    username: string,
    email: string,
    password: string,
  ): Promise<{ username: string }>;

  /**
   * function to read all user information
   * @param username
   */
  readUser(username: string): Promise<User | null>;

  readUserById(user_id: string): Promise<User | null>;

  /**
   * function to find a user by username or email for auth
   * @param identifier
   */
  findByUsernameOrEmail(identifier: string): Promise<User | null>;

  /**
   * function to update username
   * @param username
   * @param new_username
   */
  updateUsername(username: string, new_username: string): Promise<void>;

  /**
   * function to update email
   * @param username
   * @param new_email
   */
  updateEmail(username: string, new_email: string): Promise<void>;

  /**
   * function to update password
   * @param username
   * @param new_password
   */
  updatePassword(username: string, new_password: string): Promise<void>;

  /**
   * function to update streak
   * It changes streak and last_streak field
   * @param username
   */
  updateStreak(username: string): Promise<void>;

  /**
   * function to update premium date.
   * @param username
   * @param months
   */
  updatePremium(username: string, months: number): Promise<void>;

  /**
   * function to delete user.
   * @param username
   */
  deleteUser(username: string): Promise<void>;
}
