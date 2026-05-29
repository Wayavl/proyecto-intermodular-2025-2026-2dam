import APIException from "../api-exception.ts";

export default class UserAlreadyExists extends APIException {
    constructor(message: string = "Username is already registered")
    {
        super(message)
        this.errorCode = 401
    }
}