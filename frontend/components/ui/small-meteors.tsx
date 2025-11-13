"use client";

import React from "react";
import { cn } from "@/lib/utils";

interface SmallMeteorsProps {
  number?: number;
  className?: string;
}

export const SmallMeteors = ({
  number = 5,
  className,
}: SmallMeteorsProps) => {
  const meteors = new Array(number).fill(true);
  return (
    <div className={cn("relative w-full h-full overflow-hidden", className)}>
      {meteors.map((_, idx) => {
        // Generate random positions within the container
        const randomTop = Math.random() * 100; // 0-100% of container height
        const randomLeft = Math.random() * 100; // 0-100% of container width
        
        return (
          <span
            key={"small-meteor" + idx}
            className={cn(
              "absolute h-0.5 w-0.5 rounded-[9999px] bg-slate-400 shadow-[0_0_0_1px_#ffffff10] rotate-[215deg]",
              "before:content-[''] before:absolute before:top-1/2 before:transform before:-translate-y-[50%] before:w-[30px] before:h-[1px] before:bg-gradient-to-r before:from-[#94a3b8] before:to-transparent"
            )}
            style={{
              top: `${randomTop}%`,
              left: `${randomLeft}%`,
              animationDelay: Math.random() * (0.8 - 0.2) + 0.2 + "s",
              animationDuration: Math.floor(Math.random() * (3 - 1) + 1) + "s",
            }}
          ></span>
        );
      })}
    </div>
  );
};

