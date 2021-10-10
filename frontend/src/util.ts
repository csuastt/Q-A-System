export function timestampToDate(timestamp: number): Date {
    return new Date(timestamp * 1000);
}

const dateTimeFormat = Intl.DateTimeFormat("zh-CN", {
    dateStyle: "short",
    timeStyle: "short",
});

export function formatTimestamp(timestamp: number): string {
    return dateTimeFormat.format(new Date(timestamp));
}
