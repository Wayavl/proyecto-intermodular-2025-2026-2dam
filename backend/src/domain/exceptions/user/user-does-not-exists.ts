import APIException from "../api-exception.ts";

export default class UserDoesNotExists extends APIException {
    constructor(message: string = "User or password does not match")
    {
        super(message)
        this.errorCode = 401
    }
}