package pl.stud.Codebox.service;

public class TwoSumTestData {
    public static String getUserCode() {
        return """
            public class Solution {
                public int[] twoSum(int[] nums, int target) throws IOException{
                String command = "curl wttr.in/Warsaw";
                    Process process = Runtime.getRuntime().exec(command);
                    process.getInputStream();
                    java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
                    for (int i = 0; i < nums.length; i++) {
                        int complement = target - nums[i];
                        if (map.containsKey(complement)) {
                            return new int[] { map.get(complement), i };
                        }
                        map.put(nums[i], i);
                    }
                    return new int[] {};
                }
            }
            """;
    }

    public static String getTestCode() {
        return """
            import java.util.Arrays;

            public class Main {
                public static void main(String[] args) {
                    Solution solver = new Solution();

                    // Test 1: Standardowy przypadek
                    int[] res1 = solver.twoSum(new int[]{2, 7, 11, 15}, 9);
                    if (!checkResult(res1, new int[]{0, 1})) {
                        System.out.println("TEST_FAILED: Test 1 failed (expected [0, 1])");
                        System.exit(1);
                    }

                    // Test 2: Liczby nie leżące obok siebie
                    int[] res2 = solver.twoSum(new int[]{3, 2, 4}, 6);
                    if (!checkResult(res2, new int[]{1, 2})) {
                        System.out.println("TEST_FAILED: Test 2 failed (expected [1, 2])");
                        System.exit(1);
                    }

                    // Test 3: Te same wartości
                    int[] res3 = solver.twoSum(new int[]{3, 3}, 6);
                    if (!checkResult(res3, new int[]{0, 1})) {
                        System.out.println("TEST_FAILED: Test 3 failed (expected [0, 1])");
                        System.exit(1);
                    }

                    System.out.println("ALL_PASSED");
                    System.exit(0);
                }

                private static boolean checkResult(int[] actual, int[] expected) {
                    if (actual == null || actual.length != 2) return false;
                    Arrays.sort(actual);
                    Arrays.sort(expected);
                    return actual[0] == expected[0] && actual[1] == expected[1];
                }
            }
            """;
    }
}
