"use client";

import { useEffect } from "react";

/**
 * Suppresses harmless browser extension errors from appearing in the console.
 * These errors are caused by browser extensions (like LastPass, password managers, etc.)
 * trying to communicate with the page but the message channel closes before they can respond.
 */
export function ErrorSuppression() {
  useEffect(() => {
    // Suppress browser extension errors that are harmless
    const handleError = (e: ErrorEvent) => {
      const message = e.message || "";
      // Suppress common browser extension errors
      if (
        message.includes("message channel closed") ||
        message.includes("Extension context invalidated") ||
        message.includes("Receiving end does not exist") ||
        message.includes("chrome-extension://") ||
        message.includes("moz-extension://")
      ) {
        e.preventDefault();
        return false;
      }
    };

    // Suppress unhandled promise rejections from extensions
    const handleUnhandledRejection = (e: PromiseRejectionEvent) => {
      const message = e.reason?.message || String(e.reason || "");
      if (
        message.includes("message channel closed") ||
        message.includes("Extension context invalidated") ||
        message.includes("Receiving end does not exist") ||
        message.includes("chrome-extension://") ||
        message.includes("moz-extension://")
      ) {
        e.preventDefault();
        return false;
      }
    };

    window.addEventListener("error", handleError);
    window.addEventListener("unhandledrejection", handleUnhandledRejection);

    return () => {
      window.removeEventListener("error", handleError);
      window.removeEventListener("unhandledrejection", handleUnhandledRejection);
    };
  }, []);

  return null;
}



