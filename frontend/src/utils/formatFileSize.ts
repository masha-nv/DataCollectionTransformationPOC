export function formatFileSize(bytes: number): string {
  if (bytes >= 1_000_000) {
    return (bytes / 1_000_000).toFixed(2) + " MB";
  } else if (bytes >= 1_000) {
    return (bytes / 1_000).toFixed(1) + " KB";
  } else {
    return bytes + " bytes";
  }
}
