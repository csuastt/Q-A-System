import { useLocation } from "react-router-dom";

export function timestampToDate(timestamp: number): Date {
    return new Date(timestamp * 1000);
}

const dateTimeFormat = Intl.DateTimeFormat("zh-CN", {
    dateStyle: "short",
    timeStyle: "short",
});

export function formatTimestamp(timestamp: string): string {
    return dateTimeFormat.format(new Date(timestamp));
}

export function useQuery() {
    return new URLSearchParams(useLocation().search);
}

export function parseIntWithDefault(
    str: string | null | undefined,
    def: number
): number {
    if (str === null || str === undefined || isNaN(parseInt(str))) {
        return def;
    }
    return parseInt(str);
}
