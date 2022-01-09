#include "jvm_util.h"
#if defined(EMSCRIPTEN)
#include <emscripten.h>
#endif

char* jstring_to_chars(Instance *jstr, Runtime *runtime) {
    Instance *ptr = jstring_get_value_array(jstr, runtime);
    s32 offset = jstring_get_offset(jstr, runtime);
    s32 count = jstring_get_count(jstr, runtime);
    if (ptr && ptr->arr_body)
    {
      u16 *jchar_arr = (u16 *) ptr->arr_body;
      char *chars = (char *) malloc(count - offset + 1);
//[count - offset];
      u32 i = 0;
      for (i=0; i<count; i++) chars[i] = jchar_arr[i+offset];
      chars[count] = 0; // Terminate with null-char
      return chars;
    }
    char *empty_ch = (char *)malloc(0);
    return empty_ch;
}

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    isWebAssembly
 * Signature: ()Z
 */

s32 org_mini_util_WasmUtil_isWebAssembly(Runtime *runtime, JClass *clazz)
{
  RuntimeStack *stack = runtime->stack;
#ifdef EMSCRIPTEN
  push_int(stack, 1);
#else
  push_int(stack, 0);
#endif
  return 0;
}

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    executeJS
 * Signature: (Ljava/lang/String;)V
 */
s32 org_mini_util_WasmUtil_executeJS (Runtime *runtime, JClass *clazz)
{
#ifdef EMSCRIPTEN
  RuntimeStack *stack = runtime->stack;
  Instance *jstr = (Instance *) localvar_getRefer(runtime->localvar, 0);
  char* chars = jstring_to_chars(jstr, runtime);

  emscripten_run_script(chars);
  free(chars);
#endif
  return 0;
}
