import { useQuery } from "@tanstack/react-query";
import { CategoriesAPI } from "./api";
export function useCategories() { return useQuery({ queryKey: ["categories"], queryFn: CategoriesAPI.list }); }
