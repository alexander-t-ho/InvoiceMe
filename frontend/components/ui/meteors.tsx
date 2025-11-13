import { cn } from "@/lib/utils";
import React, { useMemo } from "react";

export const Meteors = ({
  number,
  className,
}: {
  number?: number;
  className?: string;
}) => {
  // Memoize meteor positions to avoid recalculation on every render
  const meteors = useMemo(() => {
    const count = number || 20;
    return Array.from({ length: count }, (_, idx) => ({
      id: idx,
      top: Math.random() * 100,
      left: Math.random() * 100,
      delay: Math.random() * (0.8 - 0.2) + 0.2,
      duration: Math.floor(Math.random() * (10 - 2) + 2),
    }));
  }, [number]);

  return (
    <>
      {meteors.map((meteor) => (
        <span
          key={`meteor-${meteor.id}`}
          className={cn(
            "animate-meteor-effect absolute h-0.5 w-0.5 rounded-[9999px] bg-slate-500 shadow-[0_0_0_1px_#ffffff10] rotate-[215deg]",
            "before:content-[''] before:absolute before:top-1/2 before:transform before:-translate-y-[50%] before:w-[50px] before:h-[1px] before:bg-gradient-to-r before:from-[#64748b] before:to-transparent",
            className
          )}
          style={{
            top: `${meteor.top}%`,
            left: `${meteor.left}%`,
            animationDelay: `${meteor.delay}s`,
            animationDuration: `${meteor.duration}s`,
          }}
        ></span>
      ))}
    </>
  );
};

