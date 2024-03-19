import { PageRequest } from "../sharedComponents/DisplayTable"
import { UserInformation } from "./userController";

// Define backend server port
// TODO port is configurable
export const BACKEND_PORT = 8080
export const WEBSOCKET_PATH = "/websocket/chargepoint"

function filterOrderToAPI(order: FilterOrder) {
    switch (order) {
        case "<":
            return "<";
        case ">":
            return ">";
        case "=":
            return ":";
        default:
            console.error("Unmanaged filter order type.")
    }
}

export type FilterOrder = "=" | ">" | "<"

export type TableSortType = "desc" | "asc"

export type SearchFilter = { filterField: string, filterValue: string, filterOrder?: FilterOrder }

export type SearchSort = { field: string, order: TableSortType }

export type SearchElementsParameters = {
    size: number,
    page: number,
    filters?: SearchFilter[],
    sort?: SearchSort,
}

let cachedUserInformation: UserInformation | undefined = undefined

export async function getUserInformation(): Promise<UserInformation | undefined> {
    if(cachedUserInformation) {
        return cachedUserInformation
    }
    let response = await fetch("/api/user/me")
    if (response.ok) {
        let content = await response.json();
        let userInformation = content as UserInformation
        if(!userInformation){
            return undefined
        }
        cachedUserInformation = userInformation
        return userInformation;
    }
    return undefined
}


// Fetch elements from backend using pagination, filters and sorting
export async function searchElements<T>(path: string, params?: SearchElementsParameters): Promise<PageRequest<T> | undefined> {

    let formattedRequest = path
    if (params) {
        formattedRequest += `?size=${params.size}`
        formattedRequest += `&page=${params.page}`
        if (params.filters) {
            formattedRequest += "&request=" + encodeURI(params.filters.map(filter => {
                let separator = filterOrderToAPI(filter.filterOrder ? filter.filterOrder : "=")
                return `${filter.filterField}${separator}\`${filter.filterValue}\``
            }).join(","))
        }
        if (params.sort) {
            formattedRequest += `&sortBy=${params.sort.field}&order=${params.sort.order}`
        }
    }
    let request = await fetch(formattedRequest)
    if (request.ok) {
        let content = await request.json()
        let response = (content as PageRequest<T>)
        if (response) {
            return response
        }
    }
    return undefined
}

// Fetch all elements from the backend
export async function getAllElements<T>(path: string): Promise<T[] | undefined> {
    let request = await fetch(path)
    if(request.ok){
        let content = await request.json()
        let elements = content as T[]
        if(elements){
            return elements
        }else{
            console.error("Failed to fetch elements :", content)
        }
    }else{
        console.error("Fetch elements failed, error code:" +  request.status)
    }
    return undefined
}

// Fetch an element by id
export async function getElementById<T>(path: string, id: number) {
    let request = await fetch(`${path}/${id}`)
    if(request.ok){
        let content = await request.json()
        let element = (content as T)
        if(element != null){
            return element
        }else{
            console.error("Fetch element failed " + content)
        }
    }else{
        console.error("Fetch element failed, error code:" +  request.status)
    }
    return undefined
}

export type RequestResponse<T> = {
    succes?: T,
    error?: ErrorMessage,
}

export type ErrorMessage = {
    message: string,
}

export async function createNewElement<T>(path: string, data: any): Promise<RequestResponse<T>> {
    let response = await fetch(path,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        })
    if (response.ok) {
        let result = (await response.json()) as T
        if (result) {
            return {
                succes: result
            }
        }
        console.error("Unexpected response type received: " + path, response)
    } else {
        let error = (await response.json()) as ErrorMessage
        if(error){
            return {
                error: error
            }
        }
        console.error("Create new element request failed: " + path, response)
    }
    // Return default error response
    return {
        error: {
            message: `Erreur lors de la création avec l'URL ${path}`,
        }
    }
}


export async function updateElement<T>(method: "PATCH" | "PUT", path: string, data?: any): Promise<RequestResponse<T>> {
    const request = data ? {
            method: method,
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        } :
        {
            method: method,
        }
    let response = await fetch(path, request)

    if (response.ok) {
        let result = (await response.json()) as T
        if (result) {
            return {
                succes: result
            }
        }
        console.error("Unexpected response type received: " + path, response)
    } else {
        let error = (await response.json()) as ErrorMessage
        if (error) {
            return {
                error: error
            }
        }
        console.error("Update element request failed: " + path, response)
    }
    // Return default error response
    return {
        error: {
            message: `Erreur lors de la modification avec l'URL ${path}`,
        }
    }
}
