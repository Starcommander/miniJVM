/* Header for class org_mini_util_WasmUtil */

#ifndef _Included_org_mini_util_WasmUtil
#define _Included_org_mini_util_WasmUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_mini_util_WasmUtil
 * Method:    isWebAssembly
 * Signature: ()Z
 */
s32 org_mini_util_WasmUtil_isWebAssembly(Runtime *runtime, JClass *clazz);

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    executeJS
 * Signature: (Ljava/lang/String;)V
 */
s32 org_mini_util_WasmUtil_executeJS (Runtime *runtime, JClass *clazz);

/*
 * Class:     org_mini_util_WasmUtil
 * Method:    getThreadType
 * Signature: ()I
 * Returns: 1=MainThread 2=BrowserThread 3=Both 0=None
 */
s32 org_mini_util_WasmUtil_getThreadType (Runtime *runtime, JClass *clazz);

#ifdef __cplusplus
}
#endif
#endif
