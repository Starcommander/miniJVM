#include "jvm_util.h"
#if defined(EMSCRIPTEN)
#include <emscripten.h>
#include <emscripten/threading.h>
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

s32 org_mini_util_WasmUtil_isWebAssemblyNative(Runtime *runtime, JClass *clazz)
{
  RuntimeStack *stack = runtime->stack;
#ifdef EMSCRIPTEN
  push_int(stack, 1);
#else
  push_int(stack, 0);
#endif
  return 0;
}

void emscripten_run_script_ptr(char* chars)
{
#ifdef EMSCRIPTEN
  emscripten_run_script(chars);
  free(chars);
#endif
}

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    executeJS
 * Signature: (Ljava/lang/String;Z)V
 */
s32 org_mini_util_WasmUtil_executeJS (Runtime *runtime, JClass *clazz)
{
#ifdef EMSCRIPTEN
  RuntimeStack *stack = runtime->stack;
  Instance *jstr = (Instance *) localvar_getRefer(runtime->localvar, 0);
  char* chars = jstring_to_chars(jstr, runtime);
  int forceMain = localvar_getInt(runtime->localvar, 1);
  int returnInt = localvar_getInt(runtime->localvar, 2);
  if ( (!forceMain) || emscripten_is_main_runtime_thread())
  {
    int result = 0;
    if (returnInt)
    {
      result = emscripten_run_script_int(chars);
    }
    else
    {
      emscripten_run_script(chars);
    }
    push_int(stack, result);
  }
  else if (returnInt)
  {
    int result = emscripten_sync_run_in_main_runtime_thread(EM_FUNC_SIG_II, emscripten_run_script_int, &chars);
    push_int(stack, result);
  }
  else
  {
    emscripten_async_run_in_main_runtime_thread(EM_FUNC_SIG_VI, emscripten_run_script_ptr, &chars);
    push_int(stack, 0);
    return 0; // Return, because following 'free(chars)' is executed in function later via emscripten_run_script_ptr.
  }
  free(chars);
#endif
  return 0;
}

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    getThreadType
 * Signature: ()I
 * Returns: 1=MainThread 2=BrowserThread 3=Both 0=None
 */
s32 org_mini_util_WasmUtil_getThreadType (Runtime *runtime, JClass *clazz)
{
#ifdef EMSCRIPTEN
  RuntimeStack *stack = runtime->stack;
  int isMain = emscripten_is_main_runtime_thread();
  int isBrowser = emscripten_is_main_browser_thread();
  isBrowser = isBrowser * 2;
  push_int(stack, isMain + isBrowser);
#endif
  return 0;
}

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    setMainLoop
 * Signature: (Z)V
 * Pauses or resumes the main loop
 */
s32 org_mini_util_WasmUtil_setMainLoop (Runtime *runtime, JClass *clazz)
{
#ifdef EMSCRIPTEN
  RuntimeStack *stack = runtime->stack;
  int active = localvar_getInt(runtime->localvar, 0);
  if (active) { emscripten_resume_main_loop(); }
  else { emscripten_pause_main_loop(); }
#endif
  return 0;
}
