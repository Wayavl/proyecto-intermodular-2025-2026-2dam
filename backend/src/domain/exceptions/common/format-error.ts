import APIException from "../api-exception.ts";

export default class FormatError extends APIException {
    constructor(message: string = "Format error")
    {
        super(message)
        this.errorCode = 400
    }
}