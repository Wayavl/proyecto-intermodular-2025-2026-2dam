export default class APIException extends Error {
    errorCode: number;
    constructor(message: string = "Server Error")
    {
        super();
        this.message = message;
        this.errorCode = 500;
    }
}